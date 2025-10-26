(ns moon.dev.migrations.add-obligatory-mana
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(comment

 (def file "properties.edn")

 (defn read-properties [file]
   (-> file io/resource slurp edn/read-string))

 (defn add-mana [stats]
   (if (:stats/mana stats)
     stats
     (assoc stats :stats/mana 0)))

 (defn update-creature [creature]
   (update creature :entity/stats add-mana))

 (def creature? :creature/level)

 (#'cdq.db/save!
  {:db/data (into {}
                  (for [property (read-properties file)]
                    [(:property/id property)
                     (if (creature? property)
                       (update-creature property)
                       property)]))
   :db/file (io/resource file)})

 )
