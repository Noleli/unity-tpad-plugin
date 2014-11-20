using UnityEngine;
using System.Collections;

public class tpad : MonoBehaviour {

	private static AndroidJavaObject myTpad;
	
//	private static string[] textures = new string[] { "black", "narrowstripes" };
//	private static string[] backgrounds = new string[] { "clear", "glow" };

//	public static AndroidJavaClass 

	// Use this for initialization
	void Start () {
		AndroidJNI.AttachCurrentThread();
		AndroidJavaClass javaClass = new AndroidJavaClass("edu.collablab.games.UnityTPadIOIO");
		// Debug.Log ("Start javaClass: " + javaClass);
		myTpad = javaClass.GetStatic<AndroidJavaObject>("ctx");
//		Debug.Log("Start myTPad: " + myTpad);
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	public static void setTexture(string texturename) {
		myTpad.Call ("setTexture", texturename);
	}

//	public static void setPartnerState(int state, string notiftype) {
////		Debug.Log(myTpad);
//		if(notiftype == "haptic") {
//			myTpad.Call("setTexture", textures[state]);
//		}
//		else if(notiftype == "visual") {
//			myTpad.Call("setBackgroundImage", backgrounds[state]);
//		}
//		//logging.partnerstate = state;
//	}
}
