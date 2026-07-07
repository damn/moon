(ns clojure.sound-rebuild
  (:require
            [clojure.get-user-object]
            [clojure.remove-actor]
            [clojure.set-user-object] [clojure.window :as window]
            [clojure.group :as group]
            [clojure.pack! :as pack!]
            [clojure.find-ancestor :refer [find-ancestor]]
            [clojure.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (group/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (clojure.remove-actor/f (find-ancestor actor (partial instance? window/class)))
    (pack!/f (find-ancestor table (partial instance? window/class)))
    (let [[k _] (clojure.get-user-object/f table)]
      (clojure.set-user-object/f table [k sound-name]))))
