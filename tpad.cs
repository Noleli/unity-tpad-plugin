using UnityEngine;
using System.Collections;

public class tpad : MonoBehaviour {

	private static AndroidJavaObject myTpad;
	
	private static string[] textures = new string[] { "black", "narrowstripes" };
	private static string[] backgrounds = new string[] { "clear", "glow" };

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

	public static void setPartnerState(int state, string notiftype) {
//		Debug.Log(myTpad);
		if(notiftype == "haptic") {
			myTpad.Call("setTexture", textures[state]);
		}
		else if(notiftype == "visual") {
			myTpad.Call("setBackgroundImage", backgrounds[state]);
		}
		//logging.partnerstate = state;
	}

	public static bool getButtonPressed() {
		return myTpad.Get<bool> ("buttonPressed");
	}

	public static void setButtonPressed(bool b) {
		myTpad.Set<bool> ("buttonPressed", b);
	}

	public static void showButton() {
		myTpad.Call("showButton");
	}

	public static void hideButton() {
		myTpad.Call("hideButton");
	}

	public static void setuid(int uid) {
		myTpad.Set<int>("uid", uid);
	}

	public static void updateGameState(string gs) {
		myTpad.SetStatic<string>("gameState", gs);
	}

	public static void sendToLog(string msg) {
		myTpad.Call("writeToLog", msg);
	}

	public static void zeroTime() {
		myTpad.Call ("zeroTime");
	}

	public static void uploadLog() {
		myTpad.Call("saveToDB");
	}

	public static void newLog() {
		myTpad.Call("newLogFile");
	}

	public static bool fetchplan() {
		int i = myTpad.Call<int>("getPlan");
		if(i == 0) return true;
		else return false;
	}

	public static string[,] getPlan() {
		string s = myTpad.Get<string>("plan");
		// string s = "shuffleboard,haptic,easy\nbball,visual,hard\nbball,visual,easy\n,,";
		string[] lines = s.Split('\n');
		/*if(lines[lines.Length - 1] == "") { // lines.Length--;
			List<string> list = new List<string>(lines);
			list.RemoveAt(list.Length - 1);
			lines = list.ToArray();
		}*/
		int numattrs = 3;
		string[,] plan = new string[lines.Length,numattrs]; // = new ArrayList();
		string[] attrs;
		for(int i = 0; i < lines.Length; i++) {
			attrs = lines[i].Split(',');
			if(attrs.Length == numattrs) {
				for(int j = 0; j < attrs.Length; j++) {
					if(attrs[j] != "") plan[i,j] = attrs[j];
				}
			}
			

		}
		return plan;
		// return s;
	}
}
