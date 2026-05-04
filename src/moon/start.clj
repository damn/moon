(ns moon.start
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl-app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]

            moon.application.dispose
            moon.application.resize

            [clojure.config :refer [edn-resource]]
            )
  (:gen-class))

(def state (atom nil))

(defn -main []
  (config/use-glfw-async!)
  (lwjgl-app/create (application-listener/create
                     {:create!
                      (fn []
                        (reset! state
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        {}
                                        (edn-resource "create.edn"))))

                      :dispose!
                      (fn []
                        (moon.application.dispose/do! @state))

                      :render!
                      (fn []
                        (swap! state
                               (fn [ctx]
                                 (reduce (fn [ctx [f & params]]
                                           (apply f ctx params))
                                         ctx
                                         (edn-resource "render.edn")))))

                      :resize!
                      (fn [width height]
                        (moon.application.resize/do! @state width height))

                      :pause!
                      (fn [])

                      :resume!
                      (fn [])
                      })
                    (config/create
                     {:title "Moon"
                      :windowed-mode {:width 1440
                                      :height 900}
                      :foreground-fps 60})))
