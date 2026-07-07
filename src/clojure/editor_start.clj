(ns clojure.editor-start
  (:require [clojure.create-widget]
            [clojure.edn-resource :refer [edn-resource]]
            [clojure.gdx-application :as gdx-application]
            [clojure.use-glfw-async :as use-glfw-async]
            [clojure.widget-value]))

(defn -main []
  (use-glfw-async/f)
  (gdx-application/f! (edn-resource "config/editor.edn")))
