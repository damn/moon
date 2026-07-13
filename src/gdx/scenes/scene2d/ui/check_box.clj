(ns gdx.scenes.scene2d.ui.check-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn create [text skin]
  (check-box/new text skin))

(defn checked? [check-box]
  (check-box/isChecked check-box))

(defn set-checked! [check-box checked?]
  (check-box/setChecked check-box checked?))
