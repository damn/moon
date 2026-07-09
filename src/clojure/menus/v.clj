(ns clojure.menus.v
  (:require [clojure.menus.ctx-data :as ctx-data]
            [clojure.menus.debug-flags :as debug-flags]
            [clojure.menus.help :as help]
            [clojure.menus.select-world :as select-world]))

(def v
  [ctx-data/item
   debug-flags/item
   help/item
   select-world/item])
