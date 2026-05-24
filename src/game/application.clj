(ns game.application
  (:require [clojure.config :refer [edn-resource]]
            [clojure.impl]
            [clojure.gdx.application-listener :as listener]
            [clojure.gdx.gdx :as gdx]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application-configuration :as config])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [requires
                create
                dispose!
                render
                resize!]
         :as config} (edn-resource "start.edn")]
    (run! require requires)

    (clojure.impl/load!)
    (config/use-glfw-async!)
    (application/create (listener/create
                         {:create!
                          (fn []
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            (gdx/app)
                                            create)))

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
                                             render))))

                          :resize!
                          (fn [width height]
                            (resize! @state width height))

                          :pause!
                          (fn [])

                          :resume!
                          (fn [])})
                        (config/create config))))
