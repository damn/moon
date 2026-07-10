(ns clojure.editor.input
  (:require [gdl.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (application/get-input app)))
