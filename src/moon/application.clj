(ns moon.application
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.backends.lwjgl3.config :as config])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [create-pipeline
                dispose!
                render-pipeline
                resize!
                config
                ]} (edn-resource "game.edn")]
    (config/use-glfw-async!)
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            {}
                                            create-pipeline)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state
                                   (fn [ctx]
                                     (reduce (fn [ctx [f & params]]
                                               (apply f ctx params))
                                             ctx
                                             render-pipeline))))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_]))
                        (config/create config))))
