(ns moon.application
  (:require [gdl.application]
            [moon.core :refer [call edn-resource]])
  (:gen-class))

(def state (atom nil))

(defn start! [config]
  (let [create!  (:create!  config)
        dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)
        listener (reify gdl.application/Listener
                   (create! [_ app]
                     (reset! state (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           {:ctx/app app}
                                           create!)))

                   (dispose! [_]
                     (dispose! @state))

                   (render! [_]
                     (swap! state (fn [ctx]
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            ctx
                                            render!))))

                   (resize! [_ width height]
                     (resize! @state width height))

                   (pause! [_])

                   (resume! [_]))]
    (gdl.application/start! listener config)))

(defn -main []
  (run! call (edn-resource "config.edn")))
