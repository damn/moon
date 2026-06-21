(ns clojure.actor.toggle-visible
  (:require [clojure.actor.set-visible :refer [set-visible!]]
            [clojure.actor.visible :refer [visible?]]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))
