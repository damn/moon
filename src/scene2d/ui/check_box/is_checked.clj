(ns scene2d.ui.check-box.is-checked
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn f [check-box]
  (check-box/checked? check-box))
