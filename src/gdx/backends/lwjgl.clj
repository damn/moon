(ns gdx.backends.lwjgl
  (:require [clojure.gdx :as gdx]
            [clojure.gdx.application-listener :as application-listener]
            [clojure.gdx.backends.lwjgl.application :as lwjgl3-application]
            [clojure.gdx.backends.lwjgl.application-config :as lwjgl3-application-configuration]))

(def use-glfw-async! lwjgl3-application-configuration/use-glfw-async!)

(defn application! [config]
  (lwjgl3-application/create (application-listener/create
                              (let [state @(:state-var config)]
                                {:create!  (fn []
                                             (let [app (gdx/app)]
                                               (reset! state ((:create! config) app (:create-params config)))))
                                 :dispose! (fn []
                                             ((:dispose! config) @state))
                                 :render!  (fn []
                                             (swap! state (:render! config) (:render-params config)))
                                 :resize!  (fn [width height]
                                             ((:resize! config) @state width height))
                                 :pause!   (fn [])
                                 :resume!  (fn [])}))
                             (lwjgl3-application-configuration/create config)))
