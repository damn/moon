(ns scene2d.ui.button-group.remove
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]))

(defn f! [button-group button]
  (button-group/remove! button-group button))
