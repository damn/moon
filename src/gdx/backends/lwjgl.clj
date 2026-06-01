(ns gdx.backends.lwjgl
  (:require [clojure.gdx :as gdx]
            [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(def use-glfw-async! lwjgl3-application-configuration/use-glfw-async!)

(defn application! [config]
  (lwjgl3-application/create (application-listener/create
                              (let [state @(:state-var config)]
                                {:create!  (fn []
                                             (let [app (gdx/app)]
                                               ; (set! Gdx/app nil)
                                               ; (set! Gdx/audio nil)
                                               ; (set! Gdx/files nil)
                                               ; (set! Gdx/graphics nil)
                                               ; (set! Gdx/input nil)
                                               ; (set! Gdx/net nil)
                                               ; ; TODO and set according to needs
                                               ; ; -> know what needs what
                                               ; ; and unset again
                                               ; ; with-gdx-state
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
