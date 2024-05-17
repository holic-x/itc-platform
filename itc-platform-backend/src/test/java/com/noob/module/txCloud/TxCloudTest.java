package com.noob.module.txCloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
public class TxCloudTest {

    @Value("${txCloud.dev-COS.secretId}")
    private String secretId;

    @Value("${txCloud.dev-COS.secretKey}")
    private String secretKey;

    @Value("${txCloud.dev-COS.appId}")
    private String appId;

    @Value("${txCloud.dev-COS.bucket}")
    private String bucket;

    // cos客户端定义
    private COSClient cosClient;

    /**
     * 初始化（每个测试方法执行前执行初始化）
     */
    @BeforeEach
    public void initClient(){
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId,secretKey);
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));
        // 生成cos客户端
        cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * 测试腾讯云COS服务
     */
    @Test
    public void testGetBucket() {
        List<Bucket> buckets = cosClient.listBuckets();
        for (Bucket bucketElement : buckets) {
            String bucketName = bucketElement.getName();
            String bucketLocation = bucketElement.getLocation();
            System.out.println("存储桶："+bucketName+"-"+bucketLocation);
        }
    }

    /**
     * 创建存储桶
     */
    @Test
    public void testCreateBucket() {
        String bucketName = bucket + "-" + appId; // 存储桶名称，格式：Bucket-APPID
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        Bucket bucketResult = cosClient.createBucket(createBucketRequest);
    }


    /**
     * 上传指定对象
     */
    @Test
    public void uploadObj(){
        // 指定上传文件（例如此处上传一个照片）
        String  localFilePath = "/Users/holic-x/Desktop/mine/logo.jpg";
        // 指定要上传的文件
        File localFile = new File(localFilePath);
        // 指定文件将要存放的存储桶
        String bucketName = bucket + "-" + appId;
        // 对象key（存储桶的唯一标识）：指定文件上传到 COS 上的路径，即对象键。例如此处设定表示将文件 logo.jpg 上传到 folder 路径下
        String key = "folder/logo.jpg";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        // 执行文件上传操作
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        System.out.println(putObjectResult);
    }

    /**
     * 查询对象列表
     */
    @Test
    public void getObj(){
        // Bucket 的命名格式为 Bucket-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = bucket + "-" + appId;
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置 bucket 名称
        listObjectsRequest.setBucketName(bucketName);
        // prefix 表示列出的 object 的 key 以 prefix 开始
        listObjectsRequest.setPrefix("folder/");
        // deliter 表示分隔符, 设置为/表示列出当前目录下的 object, 设置为空表示列出所有的 object
        listObjectsRequest.setDelimiter("/");
        // 设置最大遍历出多少个对象, 一次 listobject 最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return;
            } catch (CosClientException e) {
                e.printStackTrace();
                return;
            }
            // common prefix表示被 delimiter 截断的路径, 如 delimter 设置为/, common prefix 则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();

            // object summary 表示所有列出的 object 列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径 key
                String key = cosObjectSummary.getKey();
                // 文件的 etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
                System.out.println("对象：" + key);
            }

            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
    }

    /**
     * 下载对象（方式1）
     */
    @Test
    public void downloadObj01() throws IOException {
        // Bucket 的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = bucket + "-" + appId;
        // 指定文件在 COS 上的路径（对象键）例如对象键为 folder/logo.jpg，则表示下载的文件 logo.jpg 在 folder 路径下
        String key = "folder/logo.jpg";
        // 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();
        // 下载对象的 CRC64
        String crc64Ecma = cosObject.getObjectMetadata().getCrc64Ecma();
        System.out.println("对象流:" + crc64Ecma);
        // 关闭输入流
        cosObjectInput.close();
    }


    /**
     * 下载对象（方式2）
     */
    @Test
    public void downloadObj02(){
        // Bucket 的命名格式为 Bucket-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = bucket + "-" + appId;
        // 指定文件在 COS 上的路径（对象键）例如对象键为 folder/logo.jpg，则表示下载的文件 logo.jpg 在 folder 路径下
        String key = "folder/logo.jpg";

        // 下载文件到本地的路径，例如 把文件下载到本地的指定路径下的文件夹中（指定下载对象）
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        String outputFilePath = "/Users/holic-x/Desktop/mine/newLogo.jpg";
        File downFile = new File(outputFilePath);
        getObjectRequest = new GetObjectRequest(bucketName, key);
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
    }

    /**
     * 删除对象(根据key值删除指定对象)
     */
    @Test
    public void delObj(){
        // Bucket 的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = bucket + "-" + appId;
        String key = "folder/logo.jpg";
        cosClient.deleteObject(bucketName, key);
    }

    /**
     * 删除桶
     */
    @Test
    public void delBucket(){
        // Bucket 的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        String bucketName = bucket + "-" + appId;
        cosClient.deleteBucket(bucketName);
    }

}
