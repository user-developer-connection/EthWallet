#include <jni.h>
#include <string.h>

JNIEXPORT jstring JNICALL
Java_com_ajoylab_blockchain_wallet_services_BCSecurityKeyStorePreApi23_getKeyString(JNIEnv *env,
                                                                                    jclass type) {
    const jstring returnValue = "ThisIsNotTheKeyYoureLookingFor!!";
    return (*env)->NewStringUTF(env, returnValue);
}

JNIEXPORT jstring JNICALL
Java_com_ajoylab_blockchain_wallet_services_BCSecurityKeyStorePreApi23_getIvString(JNIEnv *env,
                                                                                   jclass type) {
    const jstring returnValue = "NorTheInitVector";
    return (*env)->NewStringUTF(env, returnValue);
}