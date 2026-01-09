(ns moon.application.desktop
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application.configuration :as configuration]
            [clojure.walk :as walk]
            moon.ui.build.editor-window
            moon.ui.dev-menu
            moon.ui.editor.overview-window
            moon.ui.editor.window
            moon.ui.editor.widgets-impl
            moon.entity.state-impl)
  (:import (com.badlogic.gdx ApplicationListener))
  (:gen-class))

(def state (atom nil))

(defn start! [config]
  (configuration/use-glfw-async!)
  (let [create!  (:create!  config)
        dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           (gdx/context)
                                           create!)))

                   (dispose [_]
                     (dispose! @state))

                   (render [_]
                     (swap! state (fn [ctx]
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            ctx
                                            render!))))

                   (resize [_ width height]
                     (resize! @state width height))

                   (pause [_])

                   (resume [_]))]
    (application/create listener (configuration/create config))))

(defn- edn-resource [path]
  (->> path
       io/resource
       slurp
       (edn/read-string {:readers {'edn/resource edn-resource}})
       (walk/postwalk (fn [form]
                        (if (and (symbol? form) (namespace form))
                          (let [avar (requiring-resolve form)]
                            (assert avar form)
                            avar)
                          form)))))

(defn -main []
  (start! (edn-resource "config.edn")))
