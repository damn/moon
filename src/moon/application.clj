(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [moon.gdx :as gdx])
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

(defn -main []
  (let [{:keys [create-pipeline
                dispose!
                render-pipeline
                resize!] :as config} (edn-resource "game.edn")]
    (gdx/application!
     (merge config
            {:create! (fn [app]
                        (reset! state
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        {:ctx/app app}
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
                        (resize! @state width height))}))))
