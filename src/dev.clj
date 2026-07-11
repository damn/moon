(ns dev
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.nio.file Files
                          Paths
                          StandardCopyOption)))

(defn- ns->file [ns-string]
  (str "src/"
       (-> ns-string
           (str/replace "." "/")
           (str/replace "-" "_"))
       ".clj"))

(defn- matching-files [patterns]
  (->> patterns
       (mapcat #(file-seq (io/file %)))
       (filter #(.isFile ^java.io.File %))))

(defn- replace-in-file! [^java.io.File file from to]
  (let [content (slurp file)
        new-content (.replaceAll content (java.util.regex.Pattern/quote from) to)]
    (when (not= content new-content)
      (spit file new-content)
      (println "Updated:" (.getPath file)))))

(defn rename! [from to]
  (doseq [file (matching-files ["src" "resources" "test"])]
    (replace-in-file! file from to)))

(defn move [source-path target-path]
  (Files/move
   (Paths/get source-path (make-array String 0))
   (Paths/get target-path (make-array String 0))
   (into-array StandardCopyOption [StandardCopyOption/REPLACE_EXISTING])))

(defn move-and-rename! [from-ns to-ns]
  (let [from-file (ns->file from-ns)
        to-file (ns->file to-ns)]
    (println "Moving file " from-file " to " to-file)
    (move from-file to-file))
  (println "Renaming " from-ns " to " to-ns)
  (rename! from-ns to-ns))

(comment
  ;; Refactor helpers
  (move-and-rename! "clojure.shape-drawer"
                    "clojure.shape-drawer")
  (rename! "clojure.utils-click-listener"
           "clojure.scene2d.utils.click-listener")

  ;; Game scratch
  (require '[moon.db :as db]
           '[moon.application :as application]
           '[com.badlogic.gdx.gdx :as gdx])
  (application/post-runnable! (gdx/app)
                                (fn []
                                  (let [{:keys [ctx/db]
                                         :as ctx} @state]
                                    (txs/handle! ctx
                                                 [[:tx/spawn-creature
                                                   {:position [35 73]
                                                    :creature-property (db/build db :creatures/dragon-red)
                                                    :components {:entity/fsm {:fsm :fsms/npc
                                                                              :initial-state :npc-sleeping}
                                                                 :entity/faction :evil}}]])))))
