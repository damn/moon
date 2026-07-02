(ns editor.widget-value.default
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]))

(defn f
  [_  widget _schemas]
  ((get-user-object/f widget) 1))
