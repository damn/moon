(ns clojure.editor.graphics
  (:require [clojure.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/get-graphics app)))
