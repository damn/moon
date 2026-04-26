(ns moon.hiera
  (:require [clojure.string :as str]
            [hiera.main :as hiera])
  (:import (java.io File)))

(defn locked-files [^File dir]
  (->> (file-seq dir)
       (filter #(.isFile ^File %))
       (remove #(.canWrite ^File %))))

(defn file->ns [^File f]
  (let [path (.getPath f)
        rel  (second (re-find #"src[/\\](.*)" path))
        no-ext (str/replace rel #"\.(clj|cljc|cljs)$" "")]
    (-> no-ext
        (str/replace #"[\\/]" ".")   ;; / or \ → .
        (str/replace "_" "-"))))     ;; _ → -

(defn locked-namespaces []
  (sort (map file->ns (locked-files (File. "src")))))

(comment
 (clojure.pprint/pprint
  (mapv symbol (locked-namespaces))))

(def finished
  '#{
     dev,
     moon.throwable
     clojure.math
     clojure.gdx.scene2d.actor,
     }
  )

; TODO !
; size relative to lines of code !
; bigger, heavyer , slower

(comment

 ; java heap space 512m required
 (hiera/graph
  {:sources #{"src"}
   :output "target/hiera"
   :layout :horizontal
   :external false
   ;:ignore #_(set (mapv symbol (locked-namespaces)))
   ;:ignore '#{clojure, dev, clojure, com}
   })

 (hiera/graph
  {:sources #{"src"}
   :output "target/hiera"
   :layout :horizontal
   :external false
   :cluster-depth 1})


 ; TODO MAKE COLORS !!

 ; hiera.main/render-node
 ; dot node->descriptor

 ; https://github.com/ztellman/rhizome
 ; https://www.graphviz.org/docs/nodes/

 )

(comment
 (defn- render-node
   "Render graph options for a node."
   [data node]
   (let [internal? (contains? (:namespaces data) node)
         cluster (node-cluster data node)]
     {:label (if (seq cluster)
               (subs (str node) (inc (count cluster)))
               (str node))
      :color "red"
      :bgcolor "red"
      :style (if internal? :solid :dashed)}))

 ; :color doesnt work
 ; :bgcolor works
 )
