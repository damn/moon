(ns clojure.editor.graphics
  (:require [gdl.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/get-graphics app)))
