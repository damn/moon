(ns game.schema-widget.default
  (:require [moon.edn :as edn]
            [moon.schema :as schema]
            [moon.string :as string]
            [moon.ui.actor :as actor]))

(defmethod schema/create :default
  [_ v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/label
    :text (string/truncate (edn/->str v) 60)
    :skin skin}))

(defmethod schema/value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))
