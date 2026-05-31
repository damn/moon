(ns gdx.backends.lwjgl
  (:require [com.badlogic.gdx.gdx :as gdx]
            [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(def state (atom nil))

; TODO pass listener!
(defn application! [config]
  (lwjgl3-application/create (application-listener/create
                              {:create!  (fn []
                                           (reset! state ((:create! config) (gdx/app) (:create-params config))))
                               :dispose! (fn []
                                           ((:dispose! config) @state))
                               :render!  (fn []
                                           (swap! state (:render! config) (:render-params config)))
                               :resize!  (fn [width height]
                                           ((:resize! config) @state width height))
                               :pause!   (fn [])
                               :resume!  (fn [])})
                             (lwjgl3-application-configuration/create config)))
