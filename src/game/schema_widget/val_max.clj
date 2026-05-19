(ns game.schema-widget.val-max
  (:require clojure.edn
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.text-field :as text-field]
            [moon.edn :as edn]
            [moon.schema :as schema]))

(defmethod schema/create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/text-field
    :text (edn/->str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod schema/value :s/val-max
  [_  widget _schemas]
  (clojure.edn/read-string (text-field/text widget)))
