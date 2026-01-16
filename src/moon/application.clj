(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(defn edn-resource [path]
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

(def state (atom nil))

(defn apply* [[f params]]
  (f params))

(defn lwjgl-app-config [config]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle (:title config))
    (.setWindowedMode (:width (:window config))
                      (:height (:window config)))
    (.setForegroundFPS (:fps config))))

(defn app-listener
  [{:keys [create!
           dispose!
           render!
           resize!]}]
  (let [[create-fn create-params] create!
        [render-fn render-params] render!]
    (reify ApplicationListener
      (create [_]
        (reset! state (create-fn Gdx/app create-params)))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render-fn render-params))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [{:keys [requires
                listener
                config]} (edn-resource "start.edn")]
    (run! require requires)
    (Lwjgl3Application. (apply* listener)
                        (apply* config))))
