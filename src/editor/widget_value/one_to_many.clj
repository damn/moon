(ns editor.widget-value.one-to-many
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.get-children :as get-children]))

(defn f
  [_  widget _schemas]
  (->> (get-children/f widget)
       (keep get-user-object/f)
       set))
