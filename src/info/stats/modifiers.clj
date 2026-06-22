(ns info.stats.modifiers
  (:require [clojure.string :as str]
            [moon.ops.info :as ops-info]))

(defn info [mods _ctx]
  (when (seq mods)
    (str/join "\n" (keep (fn [[k ops]]
                           (ops-info/f ops k)) mods))))
