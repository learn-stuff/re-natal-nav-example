(ns navigation-app.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [navigation-app.events]
            [navigation-app.subs]))

(js/require "react-native-gesture-handler")
(js/require "react-navigation")

(def ReactNative (js/require "react-native"))
(def ReactNavigation (js/require "react-navigation"))
(def app-registry (.-AppRegistry ReactNative))
(def create-app-container (.-createAppContainer ReactNavigation))
(def create-stack-navigator (.-createStackNavigator ReactNavigation))
(def create-switch-navigator (.-createSwitchNavigator ReactNavigation))
(def create-drawer-navigator (.-createDrawerNavigator ReactNavigation))
(def create-stack-navigator (.-createStackNavigator ReactNavigation))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defn LoginScreen []
  (fn [props]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-vertical 20 :text-align "center"}} "Login screen"]
     [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press (fn []
                                       (.navigate (.-navigation (clj->js props)) "FirstScreen"))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Login"]]]))

(defn FirstScreen []
  (fn [props]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "First screen"]
     [touchable-highlight {:style {:background-color "#999" :margin-bottom 20 :padding 10 :border-radius 5}
                           :on-press (fn []
                                       (.openDrawer (.-navigation (clj->js props))))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "open Drawer"]]
     [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press (fn []
                                        ;; which is equal to props.navigation.navigate("SecondScreen")
                                       (.navigate (.-navigation (clj->js props)) "SecondScreen"))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "next screen"]]]))

(defn SecondScreen []
  (fn [props]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "Second screen"]
     [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press (fn []
                                       (.goBack (.-navigation (clj->js props))))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "go back"]]]))

(defn SideMenu []
  (fn [props]
    [view {:style {:padding 20}}
     [text {:style {:font-size 30 :font-weight "100" :margin-vertical 20 :text-align "center"}} "Logo"]
     [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press (fn []
                                       (.navigate (.-navigation (clj->js props)) "LoginScreen"))}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Log out"]]]))

(defn LinkToMenu []
  (fn [props]
    (println (clj->js props))

    [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                          :on-press (fn []
                                      (.openDrawer (.-navigation (clj->js props))))}
     [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "open Drawer"]]))

(def StackNavigator
  (create-stack-navigator
   #js{:FirstScreen
       #js{:screen (r/reactify-component FirstScreen)
           :navigationOptions (fn [props] #js{:headerLeft (r/as-element [LinkToMenu props])})}
       :SecondScreen (r/reactify-component SecondScreen)}
   #js{:initialRouteName "FirstScreen"}))

(def DrawerNavigator
  (create-drawer-navigator
   #js{:SideMenu  (r/reactify-component SideMenu)
       :StackNavigator  (r/reactify-component StackNavigator)}
   #js{:initialRouteName "StackNavigator" :drawerWidth 200 :contentComponent (r/reactify-component SideMenu)}))

(def AppNavigator
  (create-switch-navigator
   #js{:LoginScreen  (r/reactify-component LoginScreen)
       :DrawerNavigator  (r/reactify-component DrawerNavigator)}
   #js{:initialRouteName "LoginScreen"}))

(defn app-root [] [:> (create-app-container AppNavigator) {}])

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "NavigationApp" #(r/reactify-component app-root)))
