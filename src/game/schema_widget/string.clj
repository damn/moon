(ns game.schema-widget.string
  (:require [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.text-field :as text-field]
            [moon.schema :as schema]))

(defmethod schema/create :s/string [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/string [_ widget _schemas]
  (text-field/text widget))
