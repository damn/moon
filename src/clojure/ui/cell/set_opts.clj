(ns clojure.ui.cell.set-opts
  (:require [clojure.ui.cell.bottom! :as bottom!]
            [clojure.ui.cell.center! :as center!]
            [clojure.ui.cell.colspan! :as colspan!]
            [clojure.ui.cell.expand! :as expand!]
            [clojure.ui.cell.expand-x! :as expand-x!]
            [clojure.ui.cell.expand-y! :as expand-y!]
            [clojure.ui.cell.fill-x! :as fill-x!]
            [clojure.ui.cell.fill-y! :as fill-y!]
            [clojure.ui.cell.height! :as height!]
            [clojure.ui.cell.left! :as left!]
            [clojure.ui.cell.pad! :as pad!]
            [clojure.ui.cell.pad-bottom! :as pad-bottom!]
            [clojure.ui.cell.pad-top! :as pad-top!]
            [clojure.ui.cell.right! :as right!]
            [clojure.ui.cell.width! :as width!]))

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
