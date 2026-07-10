(ns clojure.editor.graphics
  (:require [com.badlogic.gdx.application :as application]))

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/getGraphics app)))
