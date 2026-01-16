(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx))
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

(defn listener
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
  (run! utils/apply* (edn-resource "start.edn")))
