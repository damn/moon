(ns moon.application
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.backends.lwjgl3.application :as application])
  (:import (com.badlogic.gdx ApplicationListener))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [create-pipeline
                dispose!
                render-pipeline
                resize!
                config
                ]} (edn-resource "game.edn")]
    (application/create (reify ApplicationListener
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
                        config)))
