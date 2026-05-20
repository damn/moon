(ns game.application
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.application-configuration :as config]
            [gdl.application-listener :as listener])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application))
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
    (config/use-glfw-async!)
    (Lwjgl3Application. (application-listener/create
                         (reify listener/ApplicationListener
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

                           (resume! [_])))
                      (config/create config))))
