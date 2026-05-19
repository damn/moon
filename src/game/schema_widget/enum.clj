(ns game.schema-widget.enum
  (:require [clojure.edn]
            [moon.edn :as edn]
            [moon.schema :as schema]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.select-box :as select-box]))

(defmethod schema/create :s/enum [schema v {:keys [ctx/skin]}]
  (actor/create
   {:type :ui/select-box
    :skin skin
    :items (map edn/->str (rest schema))
    :selected (edn/->str v)}))

(defmethod schema/value :s/enum [_  widget _schemas]
  (clojure.edn/read-string (select-box/selected widget)))
