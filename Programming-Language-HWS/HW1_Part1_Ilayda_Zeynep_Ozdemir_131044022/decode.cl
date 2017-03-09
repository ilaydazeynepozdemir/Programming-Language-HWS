; *********************************************
; *  341  Programming Languages               *
; *  Fall 2016                                *
; *  Author: Liu Liu                          *
; *          Ulrich Kremer                    *
; *          Furkan Tektas , clisp            *
; *			 Ilayda Zeynep Ozdemir
; *********************************************

;; ENVIRONMENT
;; "c2i, "i2c",and "apply-list"
(load "include.cl")

;; test document
(load "document.cl")

;; test-dictionary
;; this is needed for spell checking
(load "test-dictionary.cl")

;; (load "dictionary.cl") ;;  real dictionary (45K words)


;; -----------------------------------------------------
;; HELPERS
;; *** PLACE YOUR HELPER FUNCTIONS BELOW ***
;(format t "Hello ~a ~%" (car *document*))
;
;(let (*temp*) '(k)) 


(defstruct (Controller(:conc-name cnt-)) cipherList IndexOfUsed  move) 



 ;(setf (dr-width my-door) 43.7) 
 ;(dr-width my-door) 

;(defstruct (myWord(:conc-name dr-)) indexOfDictionary Word )

;(let ((thisWord ( make-myWord :indexOfDictionary 2 : Word 'deneme))))
	;(setf thisWord )


;)

;;(nth n list)

;; ic ice liste donduruyor aldiginda get yap
(defun setElement(item all_list index )
	(append (subseq all_list 0 index) (append  (list item) (last all_list (- (length all_list) (+ index 1)))));index sonrasi ile verilen item birlestirilir
	;;sonra liste basi ile son kisimlar birlestirilir
	
)






;; Recursive solution, sharing the tail:

;;(defun insert-at (item list index)
;  (cond
 ;   ((< index 0) (error "Index too small ~A" index))
  ;  ((= index 0) (cons item list))
  ;  ((endp list) (error "Index too big"))
   ; (t (cons (first list) (insert-at item (rest list) (1- index))))))
;; -----------------------------------------------------
;; ENCODING
;; -----------------------------------------------------
(defun change(letter)
	(cond
		((eq letter 'a) 'd)
		((eq letter 'b) 'e)
		((eq letter 'c) 'f)
		((eq letter 'd) 'p)
		((eq letter 'e) 'q)
		((eq letter 'f) 'a)
		((eq letter 'g) 'b)
		((eq letter 'h) 'k)
		((eq letter 'i) 'l)
		((eq letter 'j) 'c)
		((eq letter 'k) 'r)
		((eq letter 'l) 's)
		((eq letter 'm) 't)
		((eq letter 'n) 'g)
		((eq letter 'o) 'y)
		((eq letter 'p) 'z)
		((eq letter 'q) 'h)
		((eq letter 'r) 'i)
		((eq letter 's) 'j)
		((eq letter 't) 'm)
		((eq letter 'u) 'n)
		((eq letter 'v) 'o)
		((eq letter 'w) 'u)
		((eq letter 'x) 'v)
		((eq letter 'y) 'w)
		((eq letter 'z) 'x)
		;((eq letter ' ) ' )
	)	
)
(defun changeWord(word)
	(let  (newWord) 
	(loop for y in word do 
			(push (change y) newWord )
		)
		(reverse (nth 0 (list newWord) ));return etmeyi ogrenince burayi duzelt
	)
)

(defun encodingParagraph(parag )
	(let (returnList) 
		
		(loop for x in parag do	
			(push (changeWord x) returnList )
		)
		(reverse (car (reverse(list returnList)) ));return etmeyi ogrenince burayi duzelt
	)
)

(defun encodingDocument(document )
	(let (returnList) 
		(loop for item in document do
			(push (encodingParagraph item) returnList)
		)
		(reverse (car (reverse(list returnList)) ));return etmeyi ogrenince burayi duzelt
	)
)



;format t "~a ~%" *test-document*)
;(format t "~a ~%" (encodingDocument *test-document*))


;;sozlukteki kelimeye bakip kendi kelimesinin harflerini (eger uygunsa) listeye ekler
(defun lookWord(word dictWord toControl)
	
	(let ((cipherlistTemp (cnt-cipherList toControl)))

		(loop for spell in word do

			(if (eq (nth (c2i spell) cipherlistTemp ) nil);nil ise yerine harfi koy
				
				(progn 
					(setf cipherlistTemp (setElement spell cipherlistTemp (c2i spell) )) 
					(setf  (cnt-move toControl) 1)
					(setf (cnt-cipherList toControl) cipherlistTemp )
					(format t " 
Bos Doldur-> ~a ~a |
" (nth (c2i spell)  cipherlistTemp) spell)
					(return-from lookWord toControl)
				);cipherlist update edilir (setf ile)		
				(if (eq (nth (c2i spell)  cipherlistTemp) spell );listede harfin karsiligi harfe esit yani onceden konulmus;nil degil
						(progn ;spelle esit
							(setf  (cnt-move toControl) 1)
							(setf (cnt-cipherList toControl) cipherlistTemp )
							(format t "Dolu ve simdilik dogru ~a ~a" (nth (c2i spell)  cipherlistTemp) spell)
							;(setf (cnt-IndexOfUsed toControl) (make-list 0))
							(return-from lookWord  toControl) ;

						)
						(progn ;yeri dolu demek ki nil koy ve nil return et
							(format t "Yeri dolu ~a ~a" (nth (c2i spell)  cipherlistTemp) spell)
							(setf cipherlistTemp (setElement nil cipherlistTemp (position spell cipherlistTemp :test #'equal)))
							;degilse yerine nil koyup return
							(setf (cnt-cipherList toControl) cipherlistTemp )
							(setf  (cnt-move toControl) 0)
							(return-from lookWord toControl)
						;cipherlistin yanlis harfine nil koyar ve nil dondurur
					 	)
				
				)
			)
		)

		
	)
)


(defun IsUsed(controller_ index)
	(loop for item in (cnt-IndexOfUsed controller_) do;indexOfUses icinde kullanilmis kelimenin yerini tutar
		;(format t "TEM~a" item)
		(if (eq item index)
			(return-from IsUsed 1);kullanildi
			(return-from IsUsed 0);kullanilmadi
		)
	)


)



(defun chooseIndex(controller_ index wordLength);kullanilanlar arasinda var mi diye bakiyor uzunluk kontrolu yapiyor
	;(format t "chooseIndex GELEN ~a "index)

	
	(if (eq index (+ (length  *dictionary* ) 1))
		(progn 
			(format t "SOZLUGU ASTI")
			(return-from chooseIndex 0))

	)
	(if (eq (IsUsed controller_ index) 1);kullaildi mi
		(return-from chooseIndex (chooseIndex controller_ (+ index 1) wordLength) );kullanilmis
		(progn
			(if (eq (length (nth index *dictionary*) ) wordLength)
				(return-from chooseIndex index); kullanilmamis ayni uzunluktaki yeni sozcuk
				(return-from chooseIndex (chooseIndex controller_ (+ index 1) wordLength));kullanilmamis farkli uzunlukta olursa bir sonraki indexi cagir
			)

		)
	)
)


;; -----------------------------------------------------
;; -----------------------------------------------------
;; END OF HELPER FUNCTIONS
;; -----------------------------------------------------
;; -----------------------------------------------------




;; -----------------------------------------------------
;; DECODE FUNCTIONS
;; -----------------------------------------------------
;;cipherlist olusturulacak ve return edilecek
(defun Gen-Decoder-A (paragraph)
	
	(let ((thisParagraphCont (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0):move 1)))
		(let ((dictIndex 0))
		  	(loop for i from 0 to (length paragraph) do
		  		;(format t "ele ~a " (nth i paragraph ))
		  		(let ((word (nth i paragraph )))	
		  			;(format t "WORD= ~a" word)
		  			(if (eq word nil) ()
		  				(progn
						(let ((temp (chooseIndex thisParagraphCont dictIndex (length word))));secilmesi gereken sozluk indexi
							
							(if (eq temp nil)
								(format t "BULAMADI DE RETURN ET")
								(setf dictIndex temp)
							)			
							;(format t "~a dictIndex "dictIndex)
			  				(setf thisParagraphCont (lookWord word (nth dictIndex *dictionary*) thisParagraphCont ))
				  				(if (eq (cnt-move thisParagraphCont) 0);yani kelimede hata oldugu anlasildiysa
					  					(format t "GERI DON")
					  						;(setf i (- i 1))
					  						
					  						;(Gen-Decoder-ARec (subseq all_list 0 index))
					  						;bir oncekine don
					  						;sozluk loopundan cikip kelimenin indexini bir azalttim bir onceki kelimeye bakmali!
					  					;nil ise donguden cikar yani kelime degisikligi yapar Buraya kullanilan listesi ekle!!!
					  					(progn ; move 1 ise her sey duzgun demektir kullanilanlar listesine bu kelimenin indexini ekleriz
					  						;;BURAYA BAK,
					  						
					  						(push i (cnt-IndexOfUsed thisParagraphCont))
					  						;(format t "~a" (cnt-IndexOfUsed thisParagraphCont))
					  						;(setf (cnt-cipherList thisParagraphCont) );;eger donen deger nil degil listeyse cpherlist update
					  						
					  					)

			  					)
			  			)
			  			)
			  		)
		  		)

			)
			(format t "~a "(cnt-cipherList thisParagraphCont))
			(return-from Gen-Decoder-A thisParagraphCont)

	  	)

	  
	
	)		
)






(defun MyCode-Breaker(document);simdilik bunda controlu olustur sonra gercek codeBreakerdan buna yolla

	(let ((mainController (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0):move 1)))
		;(format t "fkdnha     kljhhjhhhlşhlhlşkhklşhlkhkl")
	(loop for parag in document do

		(setf mainController (Gen-Decoder-A parag));;farkliliklari kaydet
		
	)
		
	  	;(format t "~a----
;" (cnt-cipherList mainController))
	)
	 

)

;(setq *documentEncoded* (encodingDocument *test-document*));
;(;format t "encoded  -> 
;~a 
;" *documentEncoded*)
;(format t "Original decoded  -> 
;~a 
;" *test-document*)

;(MyCode-Breaker *documentEncoded*);

(defun Code-Breaker (document decoder-function)
	(let ((mainController (make-Controller :cipherlist (make-list 26) :IndexOfUsed (make-list 0):move 1)))
		;(format t "fkdnha     kljhhjhhhlşhlhlşkhklşhlkhkl")
	(loop for parag in document do

		(setf mainController (Gen-Decoder-A parag));;farkliliklari kaydet
		
	)
		
	  	;(format t "~a----
;" (cnt-cipherList mainController))
	)
)


 ;(Code-Breaker *document*  (Gen-Decoder-A))

(defun Gen-Decoder-B-0 (paragraph)
  ;you should implement this function
)

(defun Gen-Decoder-B-1 (paragraph)
  ;you should implement this function
)
