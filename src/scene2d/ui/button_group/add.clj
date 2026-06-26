(ns scene2d.ui.button-group.add
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn f! [button-group button]
  (button-group/add! button-group button))
