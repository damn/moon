(ns game.application
  (:require [clojure.config :refer [edn-resource]]
            [gdx.api :as gdx]
            [gdl.application-listener :as listener])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [requires
                create
                dispose!
                render
                resize!
                colors
                ]
         :as config
         } (edn-resource "start.edn")]
    (run! require requires)
    (gdx/application! (reify listener/ApplicationListener
                        (create! [_ app]
                          (reset! state
                                  (reduce (fn [ctx [f & params]]
                                            (apply f ctx params))
                                          app
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
