(ns game.application
  (:require [clojure.application-listener :as listener]
            [clojure.config :refer [edn-resource]]
            [clojure.impl]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application-configuration :as config])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [requires
                create
                dispose!
                render
                resize! ]
         :as config} (edn-resource "start.edn")]
    (run! require requires)
    (clojure.impl/load!)
    (config/use-glfw-async!)
    (application/create (reify listener/ApplicationListener
                          (create! [_ application]
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            application
                                            create)))

                          (dispose! [_]
                            (dispose! @state))

                          (render! [_]
                            (swap! state
                                   (fn [ctx]
                                     (reduce (fn [ctx [f & params]]
                                               (apply f ctx params))
                                             ctx
                                             render))))

                          (resize! [_ width height]
                            (resize! @state width height))

                          (pause! [_])

                          (resume! [_]))
                        config)))
