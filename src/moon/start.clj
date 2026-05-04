(ns moon.start
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl-app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [clojure.config :refer [edn-resource]])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (config/use-glfw-async!)
  (let [{:keys [create-pipeline
                dispose!
                render-pipeline
                resize!
                config]} (edn-resource "game.edn")]
    (lwjgl-app/create (application-listener/create
                       {:create!
                        (fn []
                          (reset! state
                                  (reduce (fn [ctx [f & params]]
                                            (apply f ctx params))
                                          {}
                                          create-pipeline)))

                        :dispose!
                        (fn []
                          (dispose! @state))

                        :render!
                        (fn []
                          (swap! state
                                 (fn [ctx]
                                   (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           ctx
                                           render-pipeline))))

                        :resize!
                        (fn [width height]
                          (resize! @state width height))

                        :pause!
                        (fn [])

                        :resume!
                        (fn [])
                        })
                      (config/create config))))
