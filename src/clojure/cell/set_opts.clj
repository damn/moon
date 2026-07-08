(ns clojure.cell.set-opts
  (:require [clojure.cell.bottom! :as bottom!]
            [clojure.cell.center! :as center!]
            [clojure.cell.colspan! :as colspan!]
            [clojure.cell.expand! :as expand!]
            [clojure.cell.expand-x! :as expand-x!]
            [clojure.cell.expand-y! :as expand-y!]
            [clojure.cell.fill-x! :as fill-x!]
            [clojure.cell.fill-y! :as fill-y!]
            [clojure.cell.height! :as height!]
            [clojure.cell.left! :as left!]
            [clojure.cell.pad! :as pad!]
            [clojure.cell.pad-bottom! :as pad-bottom!]
            [clojure.cell.pad-top! :as pad-top!]
            [clojure.cell.right! :as right!]
            [clojure.cell.width! :as width!]))

(defn f [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (fill-x!/f cell)
      :fill-y?    (fill-y!/f cell)
      :expand?    (expand!/f cell)
      :expand-x?  (expand-x!/f cell)
      :expand-y?  (expand-y!/f cell)
      :bottom?    (bottom!/f cell)
      :colspan    (colspan!/f cell arg)
      :pad        (pad!/f cell arg)
      :pad-top    (pad-top!/f cell arg)
      :pad-bottom (pad-bottom!/f cell arg)
      :width      (width!/f cell arg)
      :height     (height!/f cell arg)
      :center?    (center!/f cell)
      :right?     (right!/f cell)
      :left?      (left!/f cell))))
