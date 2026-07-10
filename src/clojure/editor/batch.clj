(ns clojure.editor.batch
  (:require [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn f [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))
