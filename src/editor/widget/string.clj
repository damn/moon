(ns editor.widget.string
  (:require [editor.widget :as widget]
            [gdx.scenes.scene2d.ui.text-field :as text-field]))

(defmethod widget/create :s/string [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/string [_ widget _schemas]
  (text-field/text widget))
