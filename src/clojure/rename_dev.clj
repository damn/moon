(ns clojure.rename-dev
  (:require [clojure.move-and-rename :as move-and-rename]
            [clojure.rename :as rename]))

(comment
  ;; Move file + update all references under src/, resources/, test/
  (move-and-rename/f "clojure.g2d.area-level-grid" "clojure.g2d.area-level-grid")

  ;; Text-only rename (no file move):
  ;(rename/f "clojure.g2d.area-level-grid" "clojure.g2d.area-level-grid")
  )
