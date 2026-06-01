(ns editor.widget.boolean
  (:require [clojure.gdx.scene2d.ui.check-box :as check-box]
            [editor.widget :as widget]))

(defmethod widget/create :s/boolean
  [_ checked? {:keys [ctx/skin]}]
  (check-box/create
   {:skin skin
    :checked? checked?}))

(defmethod widget/value :s/boolean
  [_ widget _schemas]
  (check-box/checked? widget))
