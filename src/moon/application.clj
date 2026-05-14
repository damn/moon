(ns moon.application
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.backends.lwjgl3.application :as application])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [create-pipeline
                dispose!
                render-pipeline
                resize!
                config]} (edn-resource "game.edn")]
    (application/create {:create! (fn []
                                    (reset! state
                                            (reduce (fn [ctx [f & params]]
                                                      (apply f ctx params))
                                                    {}
                                                    create-pipeline)))

                         :dispose! (fn []
                                     (dispose! @state))

                         :render! (fn []
                                    (swap! state
                                           (fn [ctx]
                                             (reduce (fn [ctx [f & params]]
                                                       (apply f ctx params))
                                                     ctx
                                                     render-pipeline))))

                         :resize! (fn [width height]
                                    (resize! @state width height))
                         }
                        config)))
