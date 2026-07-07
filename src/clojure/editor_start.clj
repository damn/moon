(ns clojure.editor-start
  (:require [clojure.create-widget]
            [clojure.edn-resource :refer [edn-resource]]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.widget-value]))

(defn -main []
  (let [{:keys [listener] :as lwjgl-app-config} (edn-resource "config/editor.edn")
        [listener-f listener-params] listener]
    (lwjgl3-application/f! lwjgl-app-config (listener-f listener-params))))
