(ns moon.ui-actors.windows
  (:require [moon.ui.group :as group]))

(defn create
  [ctx actors]
  (group/create
   {:actor/name "moon.ui.windows"
    :group/actors (map (fn [sym] ((requiring-resolve sym) ctx)) actors)}))
