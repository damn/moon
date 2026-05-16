(ns clojure.gdx.scenes.scene2d.ui.select-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]
            [moon.ui.actor :as actor]
            [moon.ui.select-box]))

(defmethod actor/create :ui/select-box
  [opts]
  (select-box/create opts))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.SelectBox
  moon.ui.select-box/SelectBox
  (selected [select-box]
    (select-box/selected select-box)))
