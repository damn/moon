(ns moon.schema-widget.boolean
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [moon.schema :as schema]))

(defmethod schema/create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create {:skin skin
                     :checked? checked?}))

(defmethod schema/value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))

