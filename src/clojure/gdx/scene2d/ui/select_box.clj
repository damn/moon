(ns clojure.gdx.scene2d.ui.select-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/select-box
  [opts]
  (select-box/create opts))

(def selected select-box/selected)
