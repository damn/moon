(ns clojure.update-labels
  (:require [clojure.update-labels.elapsed-time :as elapsed-time]
            [clojure.update-labels.fps :as fps]
            [clojure.update-labels.gui :as gui]
            [clojure.update-labels.mouseover-entity-id :as mouseover-entity-id]
            [clojure.update-labels.paused :as paused]
            [clojure.update-labels.world :as world]
            [clojure.update-labels.zoom :as zoom]))

(def v
  [elapsed-time/item
   fps/item
   mouseover-entity-id/item
   paused/item
   gui/item
   world/item
   zoom/item])
